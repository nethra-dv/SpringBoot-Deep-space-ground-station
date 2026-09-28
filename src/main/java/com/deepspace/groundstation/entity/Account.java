package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chart_of_accounts")
public class Account extends BaseEntity {
    public enum Category { ASSET, LIABILITY, INCOME, EXPENSE }

    @NotBlank private String accountName;
    @NotNull @Enumerated(EnumType.STRING) private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<JournalEntryLine> journalEntryLines = new ArrayList<>();

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<JournalEntryLine> getJournalEntryLines() { return journalEntryLines; }
    public void setJournalEntryLines(List<JournalEntryLine> journalEntryLines) { this.journalEntryLines = journalEntryLines; }
}
