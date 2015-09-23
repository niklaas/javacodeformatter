import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * Formats Java code using Eclipse's code formatter.
 *
 * Based on: https://ssscripting.wordpress.com/2009/06/10/how-to-use-the-eclipse-code-formatter-from-your-code/
 */
public class Formatter {

    /**
     * Main method. Arguments: &lt;options file&gt; [&lt;file or directory&gt;]
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) throws Exception {
        Formatter f = new Formatter();
        String cfofn = args[0];
        List<String> cfoLines = FileUtils.readLines(new File(cfofn));
        f.setCodeFormatterOptionsMap
            (Formatter.parseCodeFormatterOptions(cfoLines));

        String formattedCode;
        if (args.length < 2) {
            formattedCode = f.format(IOUtils.toString(System.in));
            System.out.println(formattedCode);
        } else {
            File file = new File(args[1]);
            if (!(file.isDirectory())) {
                formattedCode = f.format(FileUtils.readFileToString(file));
                System.out.println(formattedCode);
            } else {
                f.formatInDirectory(file, true);
            }
        }

    }

    private Map<String,Object> codeFormatterOptionsMap =
        new java.util.HashMap<String,Object>();

    public Map<String,Object> getCodeFormatterOptionsMap() {
        return new java.util.HashMap<String,Object>(codeFormatterOptionsMap);
    }
    public void setCodeFormatterOptionsMap (Map<String,Object> m) {
        if (m == null) {
            codeFormatterOptionsMap.clear();
        } else {
            codeFormatterOptionsMap = new java.util.HashMap<String,Object>(m);
        }
    }

    // For tab_char, 1 = TAB, 2 = SPACE, 4 = MIXED. Use 2 because tabs are evil.

    /**
     * Parses code formatter options. Each line specifies an option as:
     * &lt;field name&gt;:&lt;type&gt;:&lt;value&gt;. Each option references
     * a public field in Eclipse's <code>DefaultCodeFormatterOptions</code>
     * class. Supported types are boolean, int, long, and string. Comment lines
     * starting with '#' and blank lines are ignored.
     *
     * @param opts code formatter options, one per string
     * @return map of field names to values (as Objects, possibly wrappers)
     */
    public static Map<String,Object> parseCodeFormatterOptions (List<String> opts) {
        Map<String,Object> m = new java.util.HashMap<String,Object>();
        for (String opt : opts) {
            if (opt.trim().equals ("") || opt.startsWith("#")) { continue; }
            String[] optParts = opt.split(":", 3);  // field:type:value
            String fieldName = optParts[0];
            String type = optParts[1];
            String valueString = optParts[2];
            Object value;
            switch (type.charAt(0)) {
                case 'b': value = Boolean.valueOf(valueString); break;
                case 'i': value = Integer.valueOf(valueString); break;
                case 'l': value = Long.valueOf(valueString); break;
                case 'd': value = Double.valueOf(valueString); break;
                default: value = valueString; break;
            }
            m.put(fieldName,value);
        }
        return m;
    }
    void modifyCFOptions(DefaultCodeFormatterOptions cfOptions) {
        for (Map.Entry<String,Object> e : codeFormatterOptionsMap.entrySet()) {
            String fieldName = e.getKey();
            try {
                Field f =
                    DefaultCodeFormatterOptions.class.getField(fieldName);
                // System.out.println (fieldName + " = " + e.getValue());
                f.set(cfOptions, e.getValue());
            } catch (NoSuchFieldException exc) {
                throw new IllegalArgumentException("Invalid option " +
                                                   fieldName);
            } catch (IllegalAccessException exc) {
                throw new IllegalArgumentException("Option inaccessible: " +
                                                   fieldName);
            }
        }
    }

    private static final String[] EXTENSIONS = { "java" };

    /**
     * Formats all files in the given directory. Formatted files are written to
     * new files starting with "formatted_".
     *
     * @param dir directory of files to format
     * @param recurse true to format files in subdirectories too
     */
    public void formatInDirectory(File dir, boolean recurse)
        throws MalformedTreeException, BadLocationException, IOException {
        Collection<File> files = FileUtils.listFiles(dir, EXTENSIONS, recurse);
        for (File f : files) {
            String formattedCode = format(FileUtils.readFileToString(f));
            File f2 = new File(f.getParentFile(), "formatted_" + f.getName());
            FileUtils.writeStringToFile(f2, formattedCode);
        }
    }

    /**
     * Formats the given source code.
     *
     * @param code unformatted code
     * @return formatted code
     */
    public String format(String code)
        throws MalformedTreeException, BadLocationException {
         
        if( code == null || code.length() <= 0 ) {
           return "";
        }
        
        Map options = new java.util.HashMap();
        options.put(JavaCore.COMPILER_SOURCE, "1.8");
        options.put(JavaCore.COMPILER_COMPLIANCE, "1.8");
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "1.8");

        DefaultCodeFormatterOptions cfOptions =
            DefaultCodeFormatterOptions.getJavaConventionsSettings();
         
        // Let default be spaces, the sanner default for most projects without project wide lints
        cfOptions.tab_char = DefaultCodeFormatterOptions.SPACE;
        
        // Let modify config file overwrite even tab_char settings, for those who want tabs however
        modifyCFOptions(cfOptions);
        CodeFormatter cf = new DefaultCodeFormatter(cfOptions, options);
         
        if(cf == null) {
           throw new RuntimeException("Failed to load CodeFormatter class object");
           //return code;
        }
           
        TextEdit te = cf.format(CodeFormatter.K_UNKNOWN, code, 0,
                                code.length(), 0, null);
           
        if(te == null) {
           throw new RuntimeException("Failed to load TextEdit class object");
           //return code;
        }
        
        IDocument dc = new Document(code);

        te.apply(dc);
        return dc.get();
    }
}
