package me.dags.daflight.handler;

import me.dags.daflight.Bind;
import me.dags.daflight.DaFlight;
import me.dags.daflight.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;


/**
 * @author dags <dags@dags.me>
 */
public class InputHandler
{
    private final MovementHandler movementHandler;
    private final Bind menu;
    private final Bind flyBind;
    private final Bind sprintBind;
    private final Bind boostBind;
    private final Bind flyUpBind;
    private final Bind flyDownBind;

    public InputHandler(Config config, MovementHandler movementHandler)
    {
        this.movementHandler = movementHandler;
        menu = Bind.from("menu", config.menu, false);
        flyBind = Bind.from("fly", config.fly, config.flyToggle);
        sprintBind = Bind.from("sprint", config.sprint, config.sprintToggle);
        boostBind = Bind.from("boost", config.boost, config.boostToggle);
        flyUpBind = Bind.from("up", config.up, true);
        flyDownBind = Bind.from("down", config.down, true);
    }

    Bind getFlyUpBind()
    {
        return flyUpBind;
    }

    Bind getFlyDownBind()
    {
        return flyDownBind;
    }

    public void handleMenuInput()
    {
        if (menu.keyPress())
        {
            DaFlight.instance().displayConfig();
        }
    }

    public void handleInput()
    {
        PlayerCapabilities capabilities = Minecraft.getMinecraft().thePlayer.capabilities;

        boolean wasFlying = movementHandler.flying;
        boolean wasSprinting = movementHandler.sprinting;

        if (flyBind.isToggle())
        {
            movementHandler.flying = flyBind.keyPress() ? !movementHandler.flying && capabilities.allowFlying : movementHandler.flying;
        }
        else
        {
            movementHandler.flying = flyBind.keyHeld() && capabilities.allowFlying;
        }

        if (sprintBind.isToggle())
        {
            movementHandler.sprinting = sprintBind.keyPress() ? !movementHandler.sprinting && capabilities.allowFlying : movementHandler.sprinting;
        }
        else
        {
            movementHandler.sprinting = sprintBind.keyHeld() && capabilities.allowFlying;
        }

        if (boostBind.isToggle())
        {
            if (boostBind.keyPress())
            {
                movementHandler.flyBoosting = movementHandler.flying != movementHandler.flyBoosting;
                movementHandler.sprintBoosting = (!movementHandler.flying && movementHandler.sprinting) != movementHandler.sprintBoosting;
            }
        }
        else
        {
            movementHandler.flyBoosting = movementHandler.flying ? boostBind.keyHeld() : movementHandler.flyBoosting;
            movementHandler.sprintBoosting = !movementHandler.flying && movementHandler.sprinting ? boostBind.keyHeld() : movementHandler.sprintBoosting;
        }

        if (DaFlight.instance().config().disabled || !capabilities.allowFlying)
        {
            boolean updated = movementHandler.flying || movementHandler.sprinting;
            movementHandler.flying = false;
            movementHandler.sprinting = false;
            movementHandler.flyBoosting = false;
            movementHandler.sprintBoosting = false;
            if (updated)
            {
                DaFlight.instance().messageHandler().sendPlayerAbilities();
            }
        }

        if (wasFlying != movementHandler.flying)
        {
            capabilities.isFlying = movementHandler.flying;
            DaFlight.instance().messageHandler().sendPlayerAbilities();
            DaFlight.instance().messageHandler().sendState(MessageHandler.CHANNEL_FLY, movementHandler.flying);
        }
        if (wasSprinting != movementHandler.sprinting)
        {
            DaFlight.instance().messageHandler().sendState(MessageHandler.CHANNEL_SPRINT, movementHandler.sprinting);
        }
    }
}
