import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File:     ScriptRipper.java
 * Author:   Daniel Deveau     100104346
 * Date:     2013/03/19
 * Version:  1.0
 *
 * Purpose:
 * 
 * Each argument should be a path to a .txt script file.
 * If the file contains any .c or .h files, they will be extracted.
 */

public class ScriptRipper
{
    public static void main(String[] args)
    {
        for (String s : args)
            parseFile(s);
    }

    /**
     * Attempt to parse the file name from a line.
     * (Looks for (roughly) cat .*\.[ch])
     * @param line the line to parse
     * @return the file name if valid or null otherwise
     */
    private static String getFileName(String line)
    {
        Pattern pattern = Pattern.compile(".*cat (.*\\.[ch]).*");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find() || line.contains("\t"))
            return null;
        return matcher.group(1);
    }

    /**
     * Attempts to parse a file.
     * @param fileName the file to parse
     */
    private static void parseFile(String fileName)
    {
        List<String> lines            = new ArrayList<String>();
        File         file             = new File(fileName);
        String       currentFileName  = null;
        int          endBrace         = -1;
        Scanner      scan             = null;
        String       directory        = file.getParent();

        if (directory == null)
            directory = "";
        else
            directory += "/";

        try
        {
            scan = new Scanner(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while (scan.hasNextLine())
        {
            String line        = scan.nextLine();
            String newFileName = getFileName(line);
            lines.add(line);

            if (line.startsWith("}") || line.startsWith("#endif"))
                endBrace = lines.size();
            if (newFileName != null)
            {
                if (endBrace < 0)
                    lines.clear();
                else if (currentFileName != null)
                {
                    writeLinesToFile(directory + currentFileName, lines,
                        endBrace);
                    endBrace = -1;
                    lines.clear();
                }
                currentFileName = newFileName;
            }
        }

        if (currentFileName != null && endBrace >= 0)
            writeLinesToFile(directory + currentFileName, lines, endBrace);
    }

    /**
     * Write numLines lines from lines to fileName.
     * 
     * Take a list of lines (lines) and write some number (numLines)
     * of them to a file by the name of fileName.
     * 
     * This removes those numLines number of lines from the list (lines)
     * in the process.
     * 
     * @param fileName the file name to attempt to write to
     * (this will fail (ungracefully) if the file already exists)
     * @param lines the list of lines
     * @param numLines the number of lines to write
     */
    private static void writeLinesToFile(String fileName,
        List<String> lines, int numLines)
    {
        File       file   = new File(fileName);
        FileWriter writer = null;

        try
        {
            if (!file.createNewFile())
                System.err.println("File already exists. (" + fileName + ")");
            else
            {
                writer = new FileWriter(file);
                for (int i = 0; i < numLines; i++)
                    writer.write(lines.remove(0) + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (writer != null)
                    writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
