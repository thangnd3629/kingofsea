package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_sea_map_refresh_transaction")
@Getter
@Setter
@Accessors(chain = true)
public class SeaMapRefreshTransaction extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Integer       totalElementDeleted;
    private Integer       totalElementNotDeleted;
    private Integer       totalElementAccordingBaseCreated;
    private Integer       totalElementAccordingZoneSeaCreated;
    private LocalDateTime timeRefresh;
    private Long          duration;

    @Transient
    private List<SeaElementConfigTransaction> seaMapConfigTransactionsModels;
    @Basic
    @Column(name = "sea_map_transaction_config", columnDefinition = "TEXT")
    private String        seaMapConfigTransactions;
    @Transient
    private List<ElementTransactionModel> elementTransactionModels;
    @Basic
    @Column(name = "sea_map_transaction", columnDefinition = "TEXT")
    private String        seaMapTransactions;

    @PostLoad
    void load() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        this.seaMapConfigTransactionsModels = mapper.readValue(this.seaMapConfigTransactions,  new TypeReference<List<SeaElementConfigTransaction>>(){});
        this.elementTransactionModels = mapper.readValue(this.seaMapTransactions,  new TypeReference<List<ElementTransactionModel>>(){});
    }

    @PrePersist
    @PreUpdate
    void persist() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        this.seaMapConfigTransactions = mapper.writeValueAsString(this.seaMapConfigTransactionsModels);
        this.seaMapTransactions = mapper.writeValueAsString(this.elementTransactionModels);
    }
}
