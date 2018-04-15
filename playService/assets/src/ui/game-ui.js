var h = require('snabbdom/h').default
var _ = require('lodash')
var constants = require('../constants')
var button = require('./button')

function render(state, game, actionHandler) {
    if (game.GameRunning) {
        var gameRunning = game.GameRunning
        return gameFrame('Game ' + state.selectedGame, [
                h('div.board', [
                    renderBoard(gameRunning),
                    renderRobots(gameRunning)
                ]),
                renderActionButtons(state, gameRunning.cycle, gameRunning.robotActions, actionHandler)],
            actionHandler)
    } else if (game.GameNotStarted) {
        return gameFrame('Game ' + state.selectedGame,
            [
                renderPlayerList('joined Players:', game.GameNotStarted.playerNames),
                button.primary(actionHandler, 'Join Game', {joinGame: state.selectedGame}),
                button.primary(actionHandler, 'Start Game', {startGame: state.selectedGame})
            ], actionHandler)
    } else if (game.GameFinished) {
        return gameFrame('Game ' + state.selectedGame + ' is finished',
            renderPlayerList('finial ranking:', game.GameFinished.players.map(function(obj){return obj.playerName})),
            actionHandler)
    } else {
        return gameFrame('GameState ' + Object.keys(game)[0] + ' is currently not supported.', undefined, actionHandler)
    }
}

function gameFrame(title, content, actionHandler) {
    return h('div', [
        h('h1', title),
        h('div', content),
        button.primary(actionHandler, 'Back to Lobby', {leaveGame: true})
    ])
}

function renderPlayerList(title, playerNames) {
    return h('h4', [
        title,
        h('ol',
            playerNames.map(function (playerName) {
                    return h('li', playerName)
                }
            ))]
    )
}

function renderBoard(game) {
    var rows = _.range(game.scenario.height).map(function (r) {
        return renderRow(game, r)
    });
    return h('board', rows)
}

function renderRow(game, row) {
    var cells = _.range(game.scenario.width).map(function (c) {
        return renderCell(game, row, c)
    })
    return h('row', cells)
}

function renderCell(game, row, column) {
    function positionEqual(pos) {
        return pos.x === column && pos.y === row
    }

    var isWallRight = false
    var isWallDown = false
    game.scenario.walls.forEach(function (wall) {
        if (positionEqual(wall.position)) {
            if (wall.direction.Down) isWallDown = true
            if (wall.direction.Right) isWallRight = true
        }
    })

    var isBeacon = positionEqual(game.scenario.beaconPosition)
    var isTarget = positionEqual(game.scenario.targetPosition)

    return h('cell', {
        class: {
            'wall-down': isWallDown,
            'wall-right': isWallRight,
            'beacon-cell': isBeacon,
            'target-cell': isTarget
        },
        props: {title: row + ", " + column}
    }, '')
}


function renderRobots(game) {
    var robots = Object.keys(game.robots).map(function (player, index) {
        var robot = game.robots[player]
        var x = robot.position.x * 50
        var y = robot.position.y * 50
        var rot = directionToRotation(robot.direction)
        return h('robot.robot' + (index % 6 + 1),
            {
                style: {
                    transform: 'translate(' + x + 'px, ' + y + 'px) rotate(' + rot + 'deg)'
                },
                props: {
                    title: player + " - " + Object.keys(robot.direction)[0],
                    id: 'robot_' + (index % 6 + 1),
                    x: x,
                    y: y,
                    rot: rot + ''
                }
            });
    })
    return h('div', robots)
}

function directionToRotation(direction) {
    if (direction.Up)
        return "0"
    else if (direction.Right)
        return "90"
    else if (direction.Down)
        return "180"
    else if (direction.Left)
        return "270"
    else
        console.error("unkown direction", direction)
}

function renderActionButtons(state, cycle, robotActions, actionHandler) {
    var headerRow = h('tr', _.range(constants.numberOfActionsPerCycle).map(function (i) {
        return h('th', 'Action ' + (i + 1))
    }))

    function actionRow(action) {
        function actionButton(slot) {
            return h('td',
                _.get(state, ['slots', slot, action]) ?
                    button.primary(actionHandler, action, {defineAction: {player: state.player, cycle, slot, action}}) :
                    button.normal(actionHandler, action, {defineAction: {player: state.player, cycle, slot, action}}))
        }

        return h('tr', _.range(constants.numberOfActionsPerCycle).map(actionButton))
    }

    return h('table', [
        headerRow,
        actionRow('MoveForward'),
        actionRow('TurnRight'),
        actionRow('TurnLeft'),
        actionRow('MoveBackward'),
    ])
}

module.exports = render