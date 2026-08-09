// sites 테이블 접근 (JPA)
package com.example.ppe_server.repository.site;

import com.example.ppe_server.entity.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    boolean existsBySiteCode(String siteCode);        // 코드 중복 확인용
    Optional<Site> findBySiteCode(String siteCode);   // 6자리 코드로 현장 찾기
}