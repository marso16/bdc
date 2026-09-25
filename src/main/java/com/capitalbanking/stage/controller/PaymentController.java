package com.capitalbanking.stage.controller;

import com.capitalbanking.stage.model.bdc.*;
import com.capitalbanking.stage.security.OAuthTokenResponse;
import com.capitalbanking.stage.service.auth.AuthTokenService;
import com.capitalbanking.stage.service.core.PingService;
import com.capitalbanking.stage.service.payment.AccountCreationService;
import com.capitalbanking.stage.service.payment.CancellationService;
import com.capitalbanking.stage.service.payment.InquiryService;
import com.capitalbanking.stage.service.payment.TransferService;
import com.capitalbanking.stage.shared.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.capitalbanking.stage.shared.MDC.withMdc;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final AuthTokenService authTokenService;
    private final TransferService transferService;
    private final CancellationService cancellationService;
    private final InquiryService inquiryService;
    private final AccountCreationService accountCreationService;
    private final PingService pingService;

    public PaymentController(AuthTokenService authTokenService,
                             TransferService transferService,
                             CancellationService cancellationService,
                             InquiryService inquiryService,
                             AccountCreationService accountCreationService,
                             PingService pingService) {
        this.authTokenService = authTokenService;
        this.transferService = transferService;
        this.cancellationService = cancellationService;
        this.inquiryService = inquiryService;
        this.accountCreationService = accountCreationService;
        this.pingService = pingService;
    }

    //    Processus d'authentification du participant ==================================================================
    @ApiOperation(value = "Processus d'authentification du participant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = OAuthTokenResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = OAuthTokenResponse.class)
    })
    @PostMapping(value = "/oauth/token")
    public ResponseEntity<OAuthTokenResponse> generateOAuthToken(HttpServletRequest httpRequest) {
        return authTokenService.issueToken(httpRequest).toResponseEntity();
    }

    //    Dépôt d'argent ===============================================================================================
    @ApiOperation(value = "Dépôt d'argent")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DepotArgentResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = DepotArgentResponse.class)
    })
    @PostMapping(value = "/depot")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DepotArgentResponse> depot(@Valid @RequestBody DepotArgentRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> transferService.depot(request));
    }

    //    Retrait d'argent =============================================================================================
    @ApiOperation(value = "Retrait d'argent")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RetraitArgentResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = RetraitArgentResponse.class)
    })
    @PostMapping(value = "/retrait")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RetraitArgentResponse> retrait(@Valid @RequestBody RetraitArgentRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> transferService.retrait(request));
    }

    //    Annulation d'une demande de débit ============================================================================
    @ApiOperation(value = "Annulation d'une demande de débit")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DebitAnnulationResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = DebitAnnulationResponse.class)
    })
    @PostMapping(value = "/annulation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DebitAnnulationResponse> debitAnnulation(@RequestBody DebitAnnulationRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> cancellationService.cancel(request));
    }

    //    Demande de renseignements sur un paiement ====================================================================
    @ApiOperation(value = "Demande de renseignements sur un paiement")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PaymentInquiryResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = PaymentInquiryResponse.class)
    })
    @PostMapping(value = "/demandePayment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentInquiryResponse> demandePayment(@RequestBody PaymentInquiryRequest request) {
        return withMdc("paymentreference", request.getPaymentreference(),
                () -> inquiryService.paymentInquiry(request));
    }

    //    Demande de solde =============================================================================================
    @ApiOperation(value = "Demande de solde")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = DemandeSoldeResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = DemandeSoldeResponse.class)
    })
    @PostMapping(value = "/demandeSolde")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DemandeSoldeResponse> demandeSolde(@RequestBody DemandeSoldeRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(),
                () -> inquiryService.balanceInquiry(request));
    }

    //    Demande de renseignements sur un compte ======================================================================
    @ApiOperation(value = "Demande de renseignements sur un compte")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = AccountInquiryResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = AccountInquiryResponse.class)
    })
    @PostMapping(value = "/demandeCompte")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountInquiryResponse> demandeCompte(@RequestBody AccountInquiryRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(),
                () -> inquiryService.accountInquiry(request));
    }

    //    Compte Creation - Creation client et compte ==================================================================
    @ApiOperation(value = "Compte Creation - Creation client et compte")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CompteCreationResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = CompteCreationResponse.class)
    })
    @PostMapping(value = "/compteCreation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CompteCreationResponse> compteCreation(@Valid @RequestBody CompteCreationRequest request) {
        return withMdc("requestID", request.getRequestID(),
                () -> accountCreationService.createAccount(request));
    }

    //    PingCGB - Verification de disponibilite du service ===========================================================
    @ApiOperation(value = "PingCGB - Verification de disponibilite du service")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PingCGBResponse.class),
            @ApiResponse(code = 400, message = "BAD REQUEST", response = PingCGBResponse.class)
    })
    @PostMapping(value = "/pingCGB")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PingCGBResponse> pingCGB(@Valid @RequestBody PingCGBRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> pingService.pingCGB(request));
    }
}