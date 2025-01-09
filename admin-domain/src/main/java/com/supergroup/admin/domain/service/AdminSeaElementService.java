package com.supergroup.admin.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.GetElementInAreaCommand;
import com.supergroup.admin.domain.repository.AdminSeaElementRepository;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminSeaElementService {
    private final AdminSeaElementRepository<SeaElement> seaElementRepository;

    public List<SeaElement> getElements(GetElementInAreaCommand command) {
        Long divWidth = command.getWidth() / 2;
        Long divHeight = command.getHeight() / 2;
        return seaElementRepository.getElementsInArea(command.getX() - divWidth, command.getX() + divWidth, command.getY() - divHeight,
                                                              command.getY() + divHeight);
    }

}
