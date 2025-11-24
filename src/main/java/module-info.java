module RoyaleCore.main {
    requires org.bukkit;
    requires static lombok;
    requires brigadier;
    requires RoyaleCore.main;
    exports dev.royalcore.api;
    exports dev.royalcore.api.registry;

}