var lobbyService = require('./lobby-service')
var gameService = require('./game-service')
var animations = require('./ui/animations')
var _ = require('lodash')
var constants = require('./constants')

function actions(state, action) {
    // lobby actions
    if (action.createGame) {
        lobbyService.createGame()
    } else if (action.deleteGame) {
        lobbyService.deleteGame(action.deleteGame)
    } else if (action.enterGame) {
        const gameId = action.enterGame
        const oldEvents = state.eventSource
        if (oldEvents)
            oldEvents.close()
        const newEvents = gameService.updates(gameId)
        const newState = Object.assign({}, state, {selectedGame: gameId, eventSource: newEvents, slots: [], logs: []})
        return gameService.getState(gameId).then(function (gameState) {
            newState.selectedGameState = gameState
            return newState
        })
    } else if (action.leaveGame) {
        const oldEvents = state.eventSource
        if (oldEvents)
            oldEvents.close()
        const newState = Object.assign({}, state)
        delete newState.selectedGame
        delete newState.eventSource
        delete newState.animations
        delete newState.logs
        return Promise.resolve(newState)
    } else if (action.definePlayerName) {
        localStorage.setItem('playerName', action.definePlayerName)
        return Promise.resolve(Object.assign({}, state, {player: action.definePlayerName}))
    }else if(action.reloadGameList){
        return lobbyService.getAllGames().then(function(gameList){
            return Object.assign({}, state, {games:gameList})
        })


        // game actions
    } else if (action.defineScenario)
        return gameService.defineGame(action.defineScenario)
            .then(_.constant(state))
    else if (action.joinGame)
        return gameService.joinGame(action.joinGame, state.player)
            .then(_.constant(state))
    else if (action.startGame)
        return gameService.startGame(action.startGame)
            .then(_.constant(state))
    else if (action.defineAction) {
        if (!state.slots)
            state.slots = []
        var slot = action.defineAction.slot
        state.slots[slot] = action.defineAction.value
        if (_.range(constants.numberOfActionsPerCycle).every(i => state.slots[i] >= 0))
            gameService.defineAction(state.selectedGame, state.player, action.defineAction.cycle, state.slots)
        return Promise.resolve(state)
    } else if(action.replayAnimations) {
        if(state.animations)
            animations.playAnimations(state.animations)
    } else {
        console.error("unknown action", action)
    }
}

module.exports = actions
