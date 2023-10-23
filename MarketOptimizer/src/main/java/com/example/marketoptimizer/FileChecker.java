package com.example.marketoptimizer;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileChecker {

    public static ArrayList<NameSettings> readJson() {
        String workingDirectory = System.getProperty("user.dir");
        String absolutePath = workingDirectory + File.separator + "settings.json";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(absolutePath), new TypeReference<ArrayList<NameSettings>>() {
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
