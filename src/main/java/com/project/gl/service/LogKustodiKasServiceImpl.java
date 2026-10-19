package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.project.gl.helper.Util;
import com.project.gl.model.projection.GlCashMovementProjection;
import com.project.gl.model.response.JournalListDTO;
import com.project.gl.model.response.LogKustodiKasDTO;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.GlCashMovementRepository;
// import com.project.gl.repo.NomorGlAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogKustodiKasServiceImpl implements LogKustodiKasService {
    
  
    @Autowired
    private  GlCashMovementRepository repository;

    private final Util util;


    public PageResponse<LogKustodiKasDTO> getData(
            int page,
            int size,
            LocalDate tanggalDari,
            LocalDate tanggalSampai,
            String branchCode,
            String direction
    ) {

        LocalDateTime dari =
                tanggalDari != null
                        ? tanggalDari.atStartOfDay()
                        : null;

        LocalDateTime sampai =
                tanggalSampai != null
                        ? tanggalSampai.plusDays(1).atStartOfDay()
                        : null;

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<GlCashMovementProjection> result =
                repository.findLogKustodiKas(
                        dari,
                        sampai,
                        branchCode,
                        direction,
                        pageable
                );

        List<LogKustodiKasDTO> content =
                result.getContent()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    private LogKustodiKasDTO toDTO(
            GlCashMovementProjection p
    ) {

        return new LogKustodiKasDTO(
                p.getId(),
                p.getJournalHeaderId(),
                p.getTransactionDate(),
                p.getJournalNo(),
                p.getTransactionNo(),
                p.getTransactionCode(),
                p.getBranchCode(),
                p.getDirection(),
                p.getAmount(),
                p.getTellerUserId(),
                p.getKasPointCode(),
                p.getCounterpartyType(),
                p.getCounterpartyRef(),
                p.getCounterpartyName(),
                p.getDescription(),
                p.getOccurredAt()
        );
    }


    @Override
    public List<LogKustodiKasDTO> getDataExport(
            String listIds, 
            LocalDate tanggalDari,
            LocalDate tanggalSampai,
            String branchCode,
            String direction

       ) {

        List<Long> ids = util.parseIdsJson(listIds);
        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        LocalDateTime dari = tanggalDari != null
                        ? tanggalDari.atStartOfDay()
                        : null;

        LocalDateTime sampai = tanggalSampai != null
                        ? tanggalSampai.plusDays(1).atStartOfDay()
                        : null;

        List<GlCashMovementProjection> rows =  repository.findLogKustodiKasExport(
                    safeIds, 
                    idsEmpty, 
                    dari,
                    sampai,
                    branchCode,
                    direction
            );

        List<LogKustodiKasDTO> data =   rows.stream()          
                    .map(p -> new LogKustodiKasDTO(
                            p.getId(),
                            p.getJournalHeaderId(),
                            p.getTransactionDate(),
                            p.getJournalNo(),
                            p.getTransactionNo(),
                            p.getTransactionCode(),
                            p.getBranchCode(),
                            p.getDirection(),
                            p.getAmount(),
                            p.getTellerUserId(),
                            p.getKasPointCode(),
                            p.getCounterpartyType(),
                            p.getCounterpartyRef(),
                            p.getCounterpartyName(),
                            p.getDescription(),
                            p.getOccurredAt()
                           
                    ))
                    .toList();
       return data;


    }

}
