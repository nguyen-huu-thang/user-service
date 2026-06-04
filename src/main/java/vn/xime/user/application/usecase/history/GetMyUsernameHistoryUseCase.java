package vn.xime.user.application.usecase.history;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.user.model.UserUsernameHistory;

import vn.xime.user.application.dto.external.history.UsernameHistoryResponse;
import vn.xime.user.application.mapper.history.UserUsernameHistoryMapper;
import vn.xime.user.application.port.out.user.UserUsernameHistoryRepository;


@Component
@RequiredArgsConstructor
public class GetMyUsernameHistoryUseCase {

    private final UserUsernameHistoryRepository userUsernameHistoryRepository;

    private final UserUsernameHistoryMapper mapper;


    @Transactional(readOnly = true)
    public List<UsernameHistoryResponse> execute(
        String identifier
    ) {

        /*
         * =========================
         * USER ID
         * =========================
         */

        Id userId = IdService.fromString(
            identifier
        );


        /*
         * =========================
         * LOAD HISTORY
         * =========================
         */

        List<UserUsernameHistory> histories =
            userUsernameHistoryRepository.findByUserId(
                userId
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            histories
        );
    }
}
