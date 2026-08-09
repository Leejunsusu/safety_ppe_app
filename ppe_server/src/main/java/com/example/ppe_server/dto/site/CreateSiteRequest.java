// 현장 생성 요청 (현장명, 주소)
package com.example.ppe_server.dto.site;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class CreateSiteRequest {
    private String siteName;
    private String address;
}