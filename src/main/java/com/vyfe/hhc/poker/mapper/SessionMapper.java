package com.vyfe.hhc.poker.mapper;

import com.vyfe.hhc.poker.SessionMsg;
import com.vyfe.hhc.repo.GGSessionMsg;
import com.vyfe.hhc.repo.GGSessionMsgRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * SessionMapper类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/22
 * Description: 映射对象
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);
    
    @Mappings({@Mapping(source = "buyInDollar", target = "cashIn")})
    GGSessionMsg msgToSession(SessionMsg msg);
}
