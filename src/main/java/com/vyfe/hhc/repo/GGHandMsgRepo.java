package com.vyfe.hhc.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * HandMsgRepo类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/18
 * Description:
 */
@Repository
public interface GGHandMsgRepo extends JpaRepository<GGHandMsg, Long> {

}
