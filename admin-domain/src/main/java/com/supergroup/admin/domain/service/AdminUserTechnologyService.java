package com.supergroup.admin.domain.service;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdateUserTechnologyCommand;
import com.supergroup.kos.building.domain.repository.persistence.technology.UserTechnologyRepository;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserTechnologyService {
    private final UserTechnologyService userTechnologyService;
    private final UserTechnologyRepository userTechnologyRepository;

    @Transactional
    public void updateUserTechnology(AdminUpdateUserTechnologyCommand command) {
        var userTechnology = userTechnologyService.findByKosProfileIdAndTechnologyCode(command.getTechnologyCode(), command.getKosProfileId());
        var isLock = command.getIsLock();
        var isResearch = command.getIsResearched();
        if (Objects.nonNull(isLock)){
            userTechnology.setIsLock(isLock);
        }
        if (Objects.nonNull(isResearch)){
            userTechnology.setIsResearched(isResearch);
        }
        userTechnologyRepository.save(userTechnology);
    }
}
