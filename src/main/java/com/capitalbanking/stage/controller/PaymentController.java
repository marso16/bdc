package com.capitalbanking.stage.controller;

import com.capitalbanking.stage.config.ApiResult;
import com.capitalbanking.stage.model.bdc.*;
import com.capitalbanking.stage.security.OAuthTokenResponse;
import com.capitalbanking.stage.service.auth.AuthTokenService;
import com.capitalbanking.stage.service.payment.*;
import com.capitalbanking.stage.shared.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

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

    //    ====================================================================================================================================
    @Operation(summary = "Processus d'authentification du participant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = OAuthTokenResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = OAuthTokenResponse.class)))
    })
    @PostMapping(value = "/oauth/token")
    public ResponseEntity<OAuthTokenResponse> generateOAuthToken(HttpServletRequest httpRequest) {
        return authTokenService.issueToken(httpRequest).toResponseEntity();
    }

    //    ====================================================================================================================================
    @Operation(summary = "Dépôt d'argent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = DepotArgentResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = DepotArgentResponse.class)))
    })
    @PostMapping(value = "/depot")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DepotArgentResponse> depot(@Valid @RequestBody DepotArgentRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> transferService.depot(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Retrait d'argent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = RetraitArgentResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = RetraitArgentResponse.class)))
    })
    @PostMapping(value = "/retrait")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RetraitArgentResponse> retrait(@Valid @RequestBody RetraitArgentRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> transferService.retrait(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Annulation d'une demande de débit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = DebitAnnulationResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = DebitAnnulationResponse.class)))
    })
    @PostMapping(value = "/annulation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DebitAnnulationResponse> debitAnnulation(@RequestBody DebitAnnulationRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> cancellationService.cancel(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Demande de renseignements sur un paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = PaymentInquiryResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = PaymentInquiryResponse.class)))
    })
    @PostMapping(value = "/demandePayment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentInquiryResponse> demandePayment(@RequestBody PaymentInquiryRequest request) {
        return withMdc("paymentreference", request.getPaymentreference(),
                () -> inquiryService.paymentInquiry(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Demande de solde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = DemandeSoldeResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = DemandeSoldeResponse.class)))
    })
    @PostMapping(value = "/demandeSolde")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DemandeSoldeResponse> demandeSolde(@RequestBody DemandeSoldeRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(),
                () -> inquiryService.balanceInquiry(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Demande de renseignements sur un compte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = AccountInquiryResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = AccountInquiryResponse.class)))
    })
    @PostMapping(value = "/demandeCompte")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountInquiryResponse> demandeCompte(@RequestBody AccountInquiryRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(),
                () -> inquiryService.accountInquiry(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "Compte Creation - Creation client et compte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = CompteCreationResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = CompteCreationResponse.class)))
    })
    @PostMapping(value = "/compteCreation")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CompteCreationResponse> compteCreation(@Valid @RequestBody CompteCreationRequest request) {
        return withMdc("requestID", request.getRequestID(),
                () -> accountCreationService.createAccount(request));
    }

    //    ====================================================================================================================================
    @Operation(summary = "PingCGB - Verification de disponibilite du service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_200, description = Constants.STATUS_OK,
                    content = @Content(schema = @Schema(implementation = PingCGBResponse.class))),
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.STATUS_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = PingCGBResponse.class)))
    })
    @PostMapping(value = "/pingCGB")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PingCGBResponse> pingCGB(@Valid @RequestBody PingCGBRequest request) {
        return withMdc("issuertrxref", request.getIssuertrxref(), () -> pingService.pingCGB(request));
    }

    private <T> ResponseEntity<T> withMdc(String key, String value, Supplier<ApiResult<T>> operation) {
        MDC.put(key, key + ":" + value);
        try {
            return operation.get().toResponseEntity();
        } finally {
            MDC.clear();
        }
    }
}