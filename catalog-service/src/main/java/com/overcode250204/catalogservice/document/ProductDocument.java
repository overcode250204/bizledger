package com.overcode250204.catalogservice.document;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * ProductDocument — Elasticsearch index document.
 * Index: "products"
 *
 * Enables full-text search on product name, description, sku.
 * Synced from catalog-service whenever a product is created or updated.
 */
@Document(indexName = "product", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "tenant_id")
    private String tenantId;

    @Field(type = FieldType.Keyword, name = "category_id")
    private String categoryId;

    @Field(type = FieldType.Keyword)
    private String sku;

    /**
     * Analyzed field — supports partial keyword match "MacBook Pro" → "macbook",
     * "pro"
     */
    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"), otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword)
    })
    private String name;

    /**
     * Full-text description — analyzed for content-level product search.
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String unit;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Boolean, name = "is_active")
    private boolean active;

    @Field(type = FieldType.Date, name = "created_at", format = DateFormat.date_optional_time)
    private OffsetDateTime createdAt;

    @Field(type = FieldType.Date, name = "updated_at", format = DateFormat.date_optional_time)
    private OffsetDateTime updatedAt;
}
