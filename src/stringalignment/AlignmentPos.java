/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringalignment;

/**
 *
 * @author superlukia
 */
public class AlignmentPos {
    private int qstart,qend,sstart,send;

    public int getQstart() {
        return qstart;
    }

    public void setQstart(int qstart) {
        this.qstart = qstart;
    }

    public int getQend() {
        return qend;
    }

    public void setQend(int qend) {
        this.qend = qend;
    }

    public int getSstart() {
        return sstart;
    }

    public void setSstart(int sstart) {
        this.sstart = sstart;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }

    @Override
    public String toString() {
        return "AlignmentPos{" + "qstart=" + qstart + ", qend=" + qend + ", sstart=" + sstart + ", send=" + send + '}';
    }
    
}
