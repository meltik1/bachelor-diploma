package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.ErlangRequestDTO;
import com.example.diplomasagaorderservice.models.DTOs.ErlangResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.StatisticsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@Slf4j
public class StatisticsService {

    private List<Long> processingTimesRenewable = new ArrayList<>();

    private List<Long> timeInQueueRenewable = new ArrayList<>();
    private StatisticsResponseDTO statisticsResponseDTO;

    private StopWatch stopWatch;



    @Value("${mail.admin-email}")
    private String email;

    ErlangService erlangService;


    EmailNotificationServiceImpl emailNotificationService;

    @Autowired
    public StatisticsService(ErlangService erlangService, EmailNotificationServiceImpl emailNotificationService) {
        this.processingTimesRenewable = new ArrayList<>();
        this.stopWatch = new StopWatch();
        this.emailNotificationService = emailNotificationService;
        stopWatch.start();
        this.erlangService = erlangService;
    }

    public void addMeasuremnt(Long processedTimeInMills, Long timeInQueueMills) {
        processingTimesRenewable.add(processedTimeInMills);
        timeInQueueRenewable.add(timeInQueueMills);
    }

    public void resetTime() {
        processingTimesRenewable.clear();
        timeInQueueRenewable.clear();
        stopWatch.reset();
        stopWatch.start();
    }

    public StatisticsResponseDTO getStatistics() throws IOException {
        double average = processingTimesRenewable.stream().mapToLong(x -> x.longValue()).average().orElse(0)/1000;
        double averageInQueue = timeInQueueRenewable.stream().mapToLong(x -> x.longValue()).average().orElse(0);
        ErlangRequestDTO erlangRequestDTO = new ErlangRequestDTO();
        erlangRequestDTO.setPercentage("0.8");
        double totalTimeSeconds = stopWatch.getTime(TimeUnit.SECONDS);
        erlangRequestDTO.setLambda(String.valueOf(processingTimesRenewable.size()/totalTimeSeconds));
        erlangRequestDTO.setMu(String.valueOf(average));
        erlangRequestDTO.setTime("0.01");
        erlangRequestDTO.setFilePath("Erlang_queue.py");
        ErlangResponseDTO channels = getChannels(erlangRequestDTO);
        StatisticsResponseDTO statisticsResponseDTO = new StatisticsResponseDTO(processingTimesRenewable.size()/totalTimeSeconds, average*1000, averageInQueue, channels.getChannelsNumber());

        return statisticsResponseDTO;
    }

    private ErlangResponseDTO getChannels(ErlangRequestDTO erlangRequestDTO) throws IOException {
        ErlangResponseDTO execute = erlangService.execute(erlangRequestDTO);
        return execute;
    }

//    private void sendMessage(StatisticsResponseDTO statisticsResponseDTO) {
//        String message = String.format("Приложение не справляется с нагрузкой \n" +
//                "Среднее время обработки {} \n" +
//                "Интенсивность запросов {} \n " +
//                "Необходимо повышение CPU до {}", statisticsResponseDTO.getAverageProcessing(), statisticsResponseDTO.getRequestsIntensivity(), statisticsResponseDTO.getChannelsNumber());
//
//        emailNotificationService.send(email, "Резкое повышение нагрузки на SAGA координатор", message);
//    }



//    @Scheduled(fixedDelay = 30000, initialDelay = 40000)
//    public void gainStatistics() throws IOException {
//        double average = processingTimesRenewable.stream().mapToLong(x -> x.longValue()).average().orElse(0)/1000.0;
//        double averageIncome = processingTimesRenewable.size()/30.0;
//
//        if (average != 0) {
//
//
//            this.statisticsResponseDTO = new StatisticsResponseDTO();
//            this.statisticsResponseDTO.setAverageProcessing(average*1000);
//            this.statisticsResponseDTO.setChannelsNumber(0);
//            this.statisticsResponseDTO.setRequestsIntensivity(averageIncome);
//
//        }
//
//        processingTimesRenewable.clear();
//    }



}
