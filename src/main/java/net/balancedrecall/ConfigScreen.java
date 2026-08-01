package net.balancedrecall;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private final BalancedRecallConfig config;
	
	private CycleButton<Boolean> takeDamageInterruptsRecall;
	private CycleButton<Boolean> takeDamagePutsMirrorOnCooldown;
	private CycleButton<Boolean> recallImpossibleWhenMonstersNearby;
	private EditBox magicMirrorUseTime;
	private EditBox magicMirrorCooldownTime;
	private EditBox dimensionalMirrorUseTime;
	private EditBox dimensionalMirrorCooldownTime;
	private CycleButton<Boolean> sleepingMatResetsPhantomTimer;

	private Button resetButton;

	public ConfigScreen(Screen parent) {
        super(Component.translatable("balancedrecall.menu.title"));
		this.parent = parent;
		this.config = BalancedRecall.config;
    }

	@Override
	protected void init() {
		// Header
		layout.addToHeader(new StringWidget(Component.translatable("config.balancedrecall.title"), this.font));

		// Footer
		LinearLayout footer = LinearLayout.horizontal().spacing(8);
		resetButton = Button.builder(Component.translatable("controls.reset"), (btn) -> this.reset()).bounds(40, 40, 120, 20).build();
		footer.addChild(resetButton);
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, (btn) -> this.onClose()).bounds(40, 40, 120, 20).build());
		this.layout.addToFooter(footer);

		// Body
		LinearLayout body = LinearLayout.vertical().spacing(8);

		// Mirror settings
		takeDamageInterruptsRecall = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULTS.takeDamageInterruptsRecall)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.take_damage_interrupts_recall"), (button, value)->{
				config.takeDamageInterruptsRecall = value;
				refreshWidgetActiveness();
			});
		body.addChild(takeDamageInterruptsRecall);

		takeDamagePutsMirrorOnCooldown = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULTS.takeDamagePutsMirrorOnCooldown)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.take_damage_puts_mirror_on_cooldown"), (button, value)->{
				config.takeDamagePutsMirrorOnCooldown = value;
				refreshWidgetActiveness();
			});
		body.addChild(takeDamagePutsMirrorOnCooldown);

		recallImpossibleWhenMonstersNearby = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULTS.recallImpossibleWhenMonstersNearby)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.recall_impossible_when_monsters_nearby"), (button, value)->{
				config.recallImpossibleWhenMonstersNearby = value;
				refreshWidgetActiveness();
			});
		body.addChild(recallImpossibleWhenMonstersNearby);

		GridLayout grid = new GridLayout();
		grid.defaultCellSetting().padding(4).alignHorizontallyCenter();
		GridLayout.RowHelper adder = grid.createRowHelper(2);
		adder.defaultCellSetting().alignHorizontallyLeft().alignVerticallyMiddle();

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.magic_mirror_use_time_seconds"), this.font));
		magicMirrorUseTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.magic_mirror_use_time_seconds")
		);
		magicMirrorUseTime.setResponder((value)->{
			try {
				config.magicMirrorUseTimeSeconds = Double.parseDouble(value);
				refreshWidgetActiveness();
			} catch (NumberFormatException e) {
				return;
			}
		});
		adder.addChild(magicMirrorUseTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.dimensional_mirror_use_time_seconds"), this.font));
		dimensionalMirrorUseTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.dimensional_mirror_use_time_seconds")
		);
		dimensionalMirrorUseTime.setResponder((value)->{
			try {
				config.dimensionalMirrorUseTimeSeconds = Double.parseDouble(value);
				refreshWidgetActiveness();
			} catch (NumberFormatException e) {
				return;
			}
		});
		adder.addChild(dimensionalMirrorUseTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds"), this.font));
		magicMirrorCooldownTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds")
		);
		magicMirrorCooldownTime.setResponder((value)->{
			try {
				config.magicMirrorCooldownTimeSeconds = Double.parseDouble(value);
				refreshWidgetActiveness();
			} catch (NumberFormatException e) {
				return;
			}
		});
		adder.addChild(magicMirrorCooldownTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds"), this.font));
		dimensionalMirrorCooldownTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds")
		);
		dimensionalMirrorCooldownTime.setResponder((value)->{
			try {
				config.dimensionalMirrorCooldownTimeSeconds = Double.parseDouble(value);
				refreshWidgetActiveness();
			} catch (NumberFormatException e) {
				return;
			}
		});
		adder.addChild(dimensionalMirrorCooldownTime);

		body.addChild(grid);

		body.addChild(new StringWidget(Component.translatable("config.balancedrecall.restart_necessary"), this.font));

		// Mat settings
		sleepingMatResetsPhantomTimer = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULTS.sleepingMatResetsPhantomTimer)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.sleeping_mat_resets_phantom_timer"), (button, value)->{
				config.sleepingMatResetsPhantomTimer = value;
				refreshWidgetActiveness();
			});
		body.addChild(sleepingMatResetsPhantomTimer);
		this.layout.addToContents(body);

		this.layout.visitWidgets(child -> {
			this.addRenderableWidget(child);
		});
		this.repositionElements();
		refreshWidgetValues();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	private void reset() {
		config.setToDefaults();
		refreshWidgetValues();
	}

	private void refreshWidgetValues() {
		takeDamageInterruptsRecall.setValue(config.takeDamageInterruptsRecall);
		takeDamagePutsMirrorOnCooldown.setValue(config.takeDamagePutsMirrorOnCooldown);
		recallImpossibleWhenMonstersNearby.setValue(config.recallImpossibleWhenMonstersNearby);
		magicMirrorUseTime.setValue(Double.toString(config.magicMirrorUseTimeSeconds));
		magicMirrorCooldownTime.setValue(Double.toString(config.magicMirrorCooldownTimeSeconds));
		dimensionalMirrorUseTime.setValue(Double.toString(config.dimensionalMirrorUseTimeSeconds));
		dimensionalMirrorCooldownTime.setValue(Double.toString(config.dimensionalMirrorCooldownTimeSeconds));
		sleepingMatResetsPhantomTimer.setValue(config.sleepingMatResetsPhantomTimer);
		refreshWidgetActiveness();
	}


	private void refreshWidgetActiveness() {
		resetButton.active = !config.areDefaults();

		// This option can only be true if the other one is, otherwise the button is disabled.
		takeDamagePutsMirrorOnCooldown.active = config.takeDamageInterruptsRecall;
		if (!config.takeDamageInterruptsRecall && takeDamagePutsMirrorOnCooldown.getValue()) {
			takeDamagePutsMirrorOnCooldown.setValue(false);
			config.takeDamagePutsMirrorOnCooldown = false;
		}
	}

	@Override
	public void onClose() {
		config.write();
		this.minecraft.gui.setScreen(this.parent);
	}
}
