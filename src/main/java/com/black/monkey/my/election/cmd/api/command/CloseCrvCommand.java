package com.black.monkey.my.election.cmd.api.command;

import com.black.monkey.my.election.core.command.BaseCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseCrvCommand extends BaseCommand {

    private String note;
}
