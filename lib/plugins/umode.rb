module UMode
  extend Discordrb::Commands::CommandContainer

  command(%i[umodes umode]) do |event|
    modez = modes(event.user).join('')
    if modez.length.positive?
      event.respond 'Your user mode is: +' + modez
    else
      event.respond 'You have no user modes.'
    end
  end
end
