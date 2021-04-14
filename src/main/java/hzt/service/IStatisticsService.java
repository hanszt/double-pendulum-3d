package hzt.service;

public interface IStatisticsService {

    IStatisticsService.ISimpleFramerateMeter getSimpleFrameRateMeter();

    interface ISimpleFramerateMeter {

        double getFrameRate();
    }
}
