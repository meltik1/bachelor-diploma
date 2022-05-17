package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.ErlangRequestDTO;
import com.example.diplomasagaorderservice.models.DTOs.ErlangResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErlangService {

    public ErlangResponseDTO execute(ErlangRequestDTO erlangRequestDTO) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", getPythonScriptPathWithArgs(erlangRequestDTO.getFilePath()), erlangRequestDTO.getLambda() , erlangRequestDTO.getMu(), erlangRequestDTO.getTime(), erlangRequestDTO.getPercentage());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());
        String s = results.get(0);
        ErlangResponseDTO erlangResponseDTO = parseResponse(s);
        return erlangResponseDTO;

    }

    private ErlangResponseDTO parseResponse(String s) throws JsonProcessingException {
        ErlangResponseDTO erlangResponseDTO = new ErlangResponseDTO();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(s);
        erlangResponseDTO.setPercentage(jsonNode.get("percentage").asDouble());
        erlangResponseDTO.setChannelsNumber(jsonNode.get("channels").asInt());
        return erlangResponseDTO;
    }

    private String getPythonScriptPathWithArgs(String filename) {
        File file = new File("src/main/resources/python/" + filename);

        return String.format("%s", file.getAbsolutePath()) ;
    }

    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
}
