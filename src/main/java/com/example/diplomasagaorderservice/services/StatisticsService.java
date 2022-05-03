package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.ErlangRequestDTO;
import com.example.diplomasagaorderservice.models.DTOs.ErlangResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.StatisticsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class StatisticsService {

    private List<Long> processingTimes = new ArrayList<>();

    @Autowired
    ErlangService erlangService;

    public void addMeasuremnt(Long processedTimeInMills) {
        processingTimes.add(processedTimeInMills);
    }

    public StatisticsResponseDTO getStatistics() {
        double average = processingTimes.stream().mapToLong(x -> x.longValue()).average().orElse(0);
        StatisticsResponseDTO statisticsResponseDTO = new StatisticsResponseDTO(processingTimes.size(), average);

        return statisticsResponseDTO;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    public void gainStatistics() throws IOException {
        double average = processingTimes.stream().mapToLong(x -> x.longValue()).average().orElse(0)/1000.0;
        double averageIncome = processingTimes.size()/30.0;
        if (average != 0) {
            ErlangRequestDTO erlangRequestDTO = new ErlangRequestDTO();
            erlangRequestDTO.setPercentage("0.8");
            erlangRequestDTO.setLambda(String.valueOf(averageIncome));
            erlangRequestDTO.setMu(String.valueOf(average));
            erlangRequestDTO.setTime("0.01");
            erlangRequestDTO.setFilePath("Erlang_queue.py");

            ErlangResponseDTO execute = erlangService.execute(erlangRequestDTO);
            log.info("Average requests processing time {}", average);
            log.info("Needed number of channels {}", execute.getChannelsNumber());
        }
        processingTimes.clear();
    }



}
