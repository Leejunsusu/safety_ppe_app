// 현장 참여 요청 (6자리 코드)
package com.example.ppe_server.dto.site;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class JoinSiteRequest {
    private String siteCode;
}