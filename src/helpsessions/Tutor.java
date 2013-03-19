/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpsessions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author drew
 */
@Entity
@Table(name = "avail")
@XmlRootElement

public class Tutor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "tutorID")
    private Integer tutorID;
    @Size(max = 45)
    @Column(name = "name")
    private String name;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 45)
    @Column(name = "email")
    private String email;
    @Column(name = "monStart")
    @Temporal(TemporalType.TIME)
    Date monStart;
    @Column(name = "monEnd")
    @Temporal(TemporalType.TIME)
    Date monEnd;
    @Column(name = "tueStart")
    @Temporal(TemporalType.TIME)
    Date tueStart;
    @Column(name = "tueEnd")
    @Temporal(TemporalType.TIME)
    Date tueEnd;
    @Column(name = "wedStart")
    @Temporal(TemporalType.TIME)
    Date wedStart;
    @Column(name = "wedEnd")
    @Temporal(TemporalType.TIME)
    Date wedEnd;
    @Column(name = "thuStart")
    @Temporal(TemporalType.TIME)
    Date thuStart;
    @Column(name = "thuEnd")
    @Temporal(TemporalType.TIME)
    Date thuEnd;
    @Size(max = 240)
    @Column(name = "specialties")
    private String specialtiesRaw;
    ArrayList<String> specialties;
    
    

    public Tutor() {       
        
    }

    public Tutor(Integer tutorID) {
        this.tutorID = tutorID;
    }

    public Integer getTutorID() {
        return tutorID;
    }

    public void setTutorID(Integer tutorID) {
        this.tutorID = tutorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getMonStart() {
        return monStart;
    }

    public void setMonStart(Date monStart) {
        this.monStart = monStart;
    }

    public Date getMonEnd() {
        return monEnd;
    }

    public void setMonEnd(Date monEnd) {
        this.monEnd = monEnd;
    }

    public Date getTueStart() {
        return tueStart;
    }

    public void setTueStart(Date tueStart) {
        this.tueStart = tueStart;
    }

    public Date getTueEnd() {
        return tueEnd;
    }

    public void setTueEnd(Date tueEnd) {
        this.tueEnd = tueEnd;
    }

    public Date getWedStart() {
        return wedStart;
    }

    public void setWedStart(Date wedStart) {
        this.wedStart = wedStart;
    }

    public Date getWedEnd() {
        return wedEnd;
    }

    public void setWedEnd(Date wedEnd) {
        this.wedEnd = wedEnd;
    }

    public Date getThuStart() {
        return thuStart;
    }

    public void setThuStart(Date thuStart) {
        this.thuStart = thuStart;
    }

    public Date getThuEnd() {
        return thuEnd;
    }

    public void setThuEnd(Date thuEnd) {
        this.thuEnd = thuEnd;
    }

    public ArrayList<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(ArrayList<String> specialties) {
        this.specialties = specialties;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tutorID != null ? tutorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tutor)) {
            return false;
        }
        Tutor other = (Tutor) object;
        if ((this.tutorID == null && other.tutorID != null) || (this.tutorID != null && !this.tutorID.equals(other.tutorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "helpsessions.Tutor[ tutorID=" + tutorID + " ]";
    }
    
}
