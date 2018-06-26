module NSFW
  extend Discordrb::Commands::CommandContainer

  command(:nsfw) do |event|
    event.message.delete
    if event.user.role?(event.server.roles.find { |role| role.name == '+n' })
      to_remove = event.server.roles.find { |role| role.name == '+n' }
      event.user.remove_role(to_remove)
      event.send_temporary_message(':+1: You can now see #mature-chatroom!', 5)
    else
      to_add = event.server.roles.find { |role| role.name == '+n' }
      event.user.add_role(to_add)
      event.send_temporary_message(':+1: You can no longer see #mature-chatroom!', 5)
    end
  end
end
