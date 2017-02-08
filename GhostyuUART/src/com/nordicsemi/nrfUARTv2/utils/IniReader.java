package com.nordicsemi.nrfUARTv2.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author James  Fancy
 */
public class IniReader {
    protected HashMap<String,Properties> sections = new HashMap<String,Properties>();
    private transient String currentSecion;
    private transient Properties current;

    public IniReader(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        read(reader);
        reader.close();
    }

    private void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line);
        }
    }

    private void parseLine(String line) {
        line = line.trim();
        if (line.matches("\\[.*\\]")) {
            //  如果是  JDK  1.4(不含1.4)以下版本,修改为  
            //  if  (line.startsWith("[")  &&  line.endsWith("]"))  {  
            currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
            //  JDK  低于  1.4  时
            //  currentSection  =  line.substring(1,  line.length()  -  1);
            current = new Properties();
            sections.put(currentSecion, current);

        } else if (line.matches(".*=.*")) {
            //  JDK  低于  1.4  时  
            //  }  else  if  (line.indexOf('=')  >=  0)  {  
            int i = line.indexOf('=');
            String name = line.substring(0, i);
            String value = line.substring(i + 1);
            current.setProperty(name, value);
        }
    }

    public String getValue(String section, String name) {
        Properties p = (Properties) sections.get(section);
        if (p == null) {
            return null;
        }
        return p.getProperty(name);
    }

    public Properties getProperties(String sectionName) {
        return sections.get(sectionName);
    }
} 