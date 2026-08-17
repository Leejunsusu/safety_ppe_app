// 노동자 소속 해제 요청 (대상 노동자 아이디)
package com.example.ppe_server.dto.site;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class RemoveWorkerRequest {
    private String targetUserId;   // 소속 해제할 노동자의 아이디
}