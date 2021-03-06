const snabbdom = require('snabbdom')
const patch = snabbdom.init([
  require('snabbdom/modules/eventlisteners').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/attributes').default,
])

const ui = require('./editor-ui')
const service = require('./editor-service')
const actions = require('./editor-actions')

function Editor(element, player, scenarioId) {
  let node = element

  function renderState(state) {
    window.currentState = state
    node = patch(node, ui(state, actionHandler(state)))
  }

  function actionHandler(state) {
    return function (action) {
      const promise = actions(state, action)
      if (promise && promise.then)
        promise.then(renderState)
    }
  }

  service.loadSingleScenario(scenarioId).then((scenario) =>
    renderState({
      player,
      scenario: scenario.scenario,
      description: scenario.description,
      scenarioId: scenario.id,
      scenarioOwner: scenario.owner
    }, element)
  ).catch(err => console.error(err))

  return this
}

module.exports = Editor
