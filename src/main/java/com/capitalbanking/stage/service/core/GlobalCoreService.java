package com.capitalbanking.stage.service.core;

import com.capitalbanking.stage.repository.core.FlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GlobalCoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalCoreService.class);

    private final FlagRepository flagRepository;

    public GlobalCoreService(FlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public boolean isCutOffTimeByFlag(String flag) {
        boolean result = false;
        try {
            result = flagRepository.isCutOffTimeByFlag(flag);
        } catch (Exception e) {
            LOGGER.error("Error in isCutOffTimeByFlag", e);
        }
        return result;
    }
}