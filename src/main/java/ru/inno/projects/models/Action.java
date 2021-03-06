package ru.inno.projects.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "actions", schema = "PUBLIC")
public class Action {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long actionId;

    private String actionName;

    private String description;

    private int teams = 0;

    private int playersOnTeam = 0;

    @OneToOne(mappedBy = "action", fetch = FetchType.LAZY, cascade =
            {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Event event;

    @OneToMany(mappedBy = "action", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private Set<PlayAction> playActions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return actionId == action.actionId
                && teams == action.teams
                && playersOnTeam == action.playersOnTeam
                && Objects.equals(actionName, action.actionName)
                && Objects.equals(description, action.description)
                && Objects.equals(event, action.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionName, description, teams, playersOnTeam, event);
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionId=" + actionId +
                ", actionName='" + actionName + '\'' +
                ", description='" + description + '\'' +
                ", teams=" + teams +
                ", playersOnTeam=" + playersOnTeam +
                '}';
    }
}
