module Rank
  extend Discordrb::Commands::CommandContainer

  command(:rank) do |event|
    rank = role(event.user, event.server).to_s
    event.respond 'Your rank is ' + rank
  end
end
