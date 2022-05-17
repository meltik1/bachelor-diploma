package com.example.diplomasagaorderservice;

import com.example.diplomasagaorderservice.models.DTOs.ErlangRequestDTO;
import com.example.diplomasagaorderservice.models.DTOs.ErlangResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PythonExecutionTet {


    @Test
    public void simpleTest() throws IOException {

        String command = "netstat -an && findstr :8080";
        Process process = Runtime.getRuntime()
                .exec(String.format(command));
        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        Integer numberOfConnections = 0;
        String line;
        while((line = inStreamReader.readLine()) != null){

           System.out.println(line);
        }
    }

    @Test
    public void givenProcessBuilder_whenStartingPipeline_thenSuccess()
            throws IOException, InterruptedException {
        List builders = Arrays.asList(

                new ProcessBuilder( "netstat", "-an"));

        List<String> strings = new ArrayList<>();
        List processes = ProcessBuilder.startPipeline(builders);
        Process last = (Process) processes.get(processes.size() - 1);

        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(last.getInputStream()));

        Integer numberOfConnections = 0;
        String line;
        while((line = inStreamReader.readLine()) != null){

            strings.add(line);
        };

        Long connectionsSize = strings.stream().filter(x -> x.contains(":8080")).count();
        connectionsSize-=2;
        connectionsSize/=2;


    }
    @Test
    public void execute() throws IOException {

        ErlangRequestDTO erlangRequestDTO = new ErlangRequestDTO();
        erlangRequestDTO.setFilePath("Erlang_queue.py");
        erlangRequestDTO.setLambda("15.0");
        erlangRequestDTO.setMu("45.0");
        erlangRequestDTO.setTime("20");
        erlangRequestDTO.setPercentage("0.8");
        ProcessBuilder processBuilder = new ProcessBuilder("python", getPythonScriptPathWithArgs(erlangRequestDTO.getFilePath()), erlangRequestDTO.getLambda() , erlangRequestDTO.getMu(), erlangRequestDTO.getTime(), erlangRequestDTO.getPercentage());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());
        String s = results.get(0);
        ErlangResponseDTO erlangResponseDTO = parseResponse(s);
        Assertions.assertEquals(6, erlangResponseDTO.getChannelsNumber());

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
        File file = new File("src/test/resources/python/" + filename);

        return String.format("%s", file.getAbsolutePath()) ;
    }

    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
}
