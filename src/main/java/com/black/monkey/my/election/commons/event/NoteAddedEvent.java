package com.black.monkey.my.election.commons.event;

import com.black.monkey.my.election.cmd.domain.NoteVO;
import com.black.monkey.my.election.core.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString

public class NoteAddedEvent extends BaseEvent {

    private NoteVO note;
}
