
package com.example.mesaclick.Model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "campana_promociones",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_campana_promocion",
        columnNames = {"id_campana", "id_promocion"}
    )
)
public class CampanaPromocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campana_promocion")
    private Long idCampanaPromocion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_campana",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_cp_campana")
    )
    private CampanaCorreo campana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_promocion",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_cp_promocion")
    )
    private Promocion promocion;

    public CampanaPromocion() {
    }

    public CampanaPromocion(
            CampanaCorreo campana,
            Promocion promocion
    ) {
        this.campana = campana;
        this.promocion = promocion;
    }

    public Long getIdCampanaPromocion() {
        return idCampanaPromocion;
    }

    public void setIdCampanaPromocion(Long idCampanaPromocion) {
        this.idCampanaPromocion = idCampanaPromocion;
    }

    public CampanaCorreo getCampana() {
        return campana;
    }

    public void setCampana(CampanaCorreo campana) {
        this.campana = campana;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }
}
