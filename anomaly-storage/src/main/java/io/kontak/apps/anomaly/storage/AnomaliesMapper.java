package io.kontak.apps.anomaly.storage;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnomaliesMapper {
    void insert(AnomalyEntity entity);

    List<AnomalyEntity> getByRoomId(String roomId);

    List<AnomalyEntity> getByThermometerId(String roomId);

    List<String> getThermometersWithAnomaliesAboveThreshold(Integer threshold);

    List<String> getRoomsWithAnomalies();

    List<String> getThermometersForRoom(String roomId);
}

