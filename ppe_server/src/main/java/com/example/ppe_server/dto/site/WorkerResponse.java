// 노동자 검색 결과 응답 (아이디, 이름, 닉네임)
package com.example.ppe_server.dto.site;

import com.example.ppe_server.entity.User;
import lombok.Getter;

@Getter
public class WorkerResponse {
    private final String userId;
    private final String name;
    private final String nickname;

    public WorkerResponse(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }
}