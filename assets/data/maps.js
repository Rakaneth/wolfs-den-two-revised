{
	wolfDen1: {
		width: 30,
		height: 30,
		dark: true,
		type: "cave",
		down: "wolfDen2",
		name: "Wolf's Den-1",
	},
	wolfDen2: {
		width: 120,
		height: 75,
		dark: false,
		type: "openDungeon",
		water: 10,
		up: "wolfDen1",
		down: "wolfDen3",
		name: "Wolf's Den-2",
	},
	wolfDen3: {
		width: 100,
		height: 100,
		dark: true,
		type: "closedDungeon",
		doors: 10,
		doubleDoors: true,
		up: "wolfDen2",
		name: "Wolf's Den-3",
	}
}