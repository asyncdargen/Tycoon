package ru.dargen.tycoon.modules.npc;

import ru.dargen.tycoon.Tycoon;
import ru.dargen.tycoon.modules.IModule;

public interface INPCModule extends IModule {

    static INPCModule get() {
        return (INPCModule) Tycoon.getInstance().getModule(INPCModule.class);
    }

    NPC register(NPC npc);

    NPC getNPC(Integer id);

    void unRegister(NPC npc);

    void unRegister(Integer id);

    void unRegisterAll();

}
