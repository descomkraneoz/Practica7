package net.iessochoa.manuelmartinez.practica7.model;


import java.util.Date;

public class Conferencia {

    private String id;
    private String nombre;
    private boolean encurso;
    private Date fecha;
    private String horario;
    private int plazas;
    private String ponente;
    private String sala;

    public Conferencia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean getEncurso() {
        return encurso;
    }

    public void setEncurso(boolean encurso) {
        this.encurso = encurso;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public String getPonente() {
        return ponente;
    }

    public void setPonente(String ponente) {
        this.ponente = ponente;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
