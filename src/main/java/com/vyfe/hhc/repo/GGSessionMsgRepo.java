package com.vyfe.hhc.repo;

import java.util.List;

import com.vyfe.hhc.poker.type.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GGSessionMsgRepo extends JpaRepository<GGSessionMsg, Long> {
    List<GGSessionMsg> findByUidAndGameTypeAndFileMd5(Integer uid, GameType type, String md5);
    
    List<GGSessionMsg> findByUidAndGameTypeAndTournamentId(Integer uid, GameType type, Long tid);
}
