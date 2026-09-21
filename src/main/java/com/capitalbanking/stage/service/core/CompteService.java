package com.capitalbanking.stage.service.core;

import com.capitalbanking.stage.model.core.Compte;
import com.capitalbanking.stage.repository.core.CompteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompteService {

    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    public Compte findCompteByCompte(String compte) {
        Optional<Compte> opt = compteRepository.findCompteByCompte(compte);
        return opt.orElse(null);
    }
}