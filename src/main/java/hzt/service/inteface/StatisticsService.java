package hzt.service.inteface;

public interface StatisticsService {

    StatisticsService.ISimpleFramerateMeter getSimpleFrameRateMeter();

    interface ISimpleFramerateMeter {

        double getFrameRate();
    }
}
