package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.ErlangRequestDTO;
import com.example.diplomasagaorderservice.models.DTOs.ErlangResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.StatisticsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Service
@EnableScheduling
@Slf4j
public class StatisticsService {

    private List<Long> processingTimes = new ArrayList<>();

    private StatisticsResponseDTO statisticsResponseDTO;

    private StopWatch stopWatch;

    @Value("${server.tomcat.threads.min-spare}")
    private String workerThreadsNumber;

    @Value("${mail.admin-email}")
    private String email;

    ErlangService erlangService;


    EmailNotificationServiceImpl emailNotificationService;

    @Autowired
    public StatisticsService(ErlangService erlangService, EmailNotificationServiceImpl emailNotificationService) {
        this.processingTimes = new ArrayList<>();
        this.statisticsResponseDTO = null;
        this.stopWatch = new StopWatch();
        this.emailNotificationService = emailNotificationService;
        stopWatch.start();
        this.erlangService = erlangService;
    }

    public void addMeasuremnt(Long processedTimeInMills) {
        processingTimes.add(processedTimeInMills);
    }

    public StatisticsResponseDTO getStatistics() throws IOException {
        if (statisticsResponseDTO != null) {
            return statisticsResponseDTO;
        }
        double average = processingTimes.stream().mapToLong(x -> x.longValue()).average().orElse(0)/1000;
        ErlangRequestDTO erlangRequestDTO = new ErlangRequestDTO();
        erlangRequestDTO.setPercentage("0.8");
        stopWatch.stop();
        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        erlangRequestDTO.setLambda(String.valueOf(processingTimes.size()/totalTimeSeconds));
        erlangRequestDTO.setMu(String.valueOf(average));
        erlangRequestDTO.setTime("0.01");
        erlangRequestDTO.setFilePath("Erlang_queue.py");
        ErlangResponseDTO channels = getChannels(erlangRequestDTO);

        StatisticsResponseDTO statisticsResponseDTO = new StatisticsResponseDTO(processingTimes.size()/30.0, average*1000, channels.getChannelsNumber());

        return statisticsResponseDTO;
    }

    private ErlangResponseDTO getChannels(ErlangRequestDTO erlangRequestDTO) throws IOException {
        ErlangResponseDTO execute = erlangService.execute(erlangRequestDTO);
        return execute;
    }

    private void sendMessage(StatisticsResponseDTO statisticsResponseDTO) {
        String message = String.format("Приложение не справляется с нагрузкой \n" +
                "Среднее время обработки {} \n" +
                "Интенсивность запросов {} \n " +
                "Необходимо повышение CPU до {}", statisticsResponseDTO.getAverageProcessing(), statisticsResponseDTO.getRequestsIntensivity(), statisticsResponseDTO.getChannelsNumber());

        emailNotificationService.send(email, "Резкое повышение нагрузки на SAGA координатор", message);
    }

    private Boolean checkLatency(Integer channelsNumber) {
        Integer threads = Integer.parseInt(workerThreadsNumber);
        if (channelsNumber > threads) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    public void gainStatistics() throws IOException {
        double average = processingTimes.stream().mapToLong(x -> x.longValue()).average().orElse(0)/1000.0;
        double averageIncome = processingTimes.size()/30.0;
        ErlangResponseDTO execute = null;
        if (average != 0) {
            ErlangRequestDTO erlangRequestDTO = new ErlangRequestDTO();
            erlangRequestDTO.setPercentage("0.8");
            erlangRequestDTO.setLambda(String.valueOf(averageIncome));
            erlangRequestDTO.setMu(String.valueOf(average));
            erlangRequestDTO.setTime("0.01");
            erlangRequestDTO.setFilePath("Erlang_queue.py");

            execute = erlangService.execute(erlangRequestDTO);
            log.info("Average requests processing time {}", average);
            log.info("Needed number of channels {}", execute.getChannelsNumber());

            this.statisticsResponseDTO = new StatisticsResponseDTO();
            this.statisticsResponseDTO.setAverageProcessing(average*1000);
            this.statisticsResponseDTO.setChannelsNumber(execute.getChannelsNumber());
            this.statisticsResponseDTO.setRequestsIntensivity(averageIncome);

            if (checkLatency(statisticsResponseDTO.getChannelsNumber())) {
                sendMessage(statisticsResponseDTO);
            }
        }

        processingTimes.clear();
    }



}
