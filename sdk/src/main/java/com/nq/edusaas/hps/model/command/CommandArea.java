package com.nq.edusaas.hps.model.command;

public class CommandArea extends CommandBase {
    public CommandArea(int code) {
        super.setCode(code);
        super.setType(COMMAND_TYPE_AREA);
    }
}
