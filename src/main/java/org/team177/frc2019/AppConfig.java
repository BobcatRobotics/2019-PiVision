package org.team177.frc2019;

public class AppConfig  {
    private int team = 177;
    private boolean server = true;

   public int getTeam() {
        return this.team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean isServer() {
        return this.server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "{" +
            " team='" + getTeam() + "'" +
            ", server='" + isServer() + "'" +
            "}";
    }
  
}