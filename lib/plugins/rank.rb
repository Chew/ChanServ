module Rank
  extend Discordrb::Commands::CommandContainer

  command(:rank) do |event|
    rank = role(event).to_s
    event.respond 'Your rank is ' + rank
  end
end
