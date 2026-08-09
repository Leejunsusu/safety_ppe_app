// 현장 정보 응답 (코드, 이름, 주소)
package com.example.ppe_server.dto.site;

import com.example.ppe_server.entity.site.Site;
import lombok.Getter;

@Getter
public class SiteResponse {
    private final String siteCode;
    private final String siteName;
    private final String address;

    public SiteResponse(Site site) {
        this.siteCode = site.getSiteCode();
        this.siteName = site.getSiteName();
        this.address = site.getAddress();
    }
}