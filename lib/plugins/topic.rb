module Topic
  extend Discordrb::Commands::CommandContainer

  command(:topic, min_args: 1) do |event, *topic|
    next unless %w[Oper Owner Admin Op Half-Op].include? role(event.user, event.server).to_s
    mode = event.channel.topic.split(' ')[0]
    event.channel.topic = mode + ' ' + topic.join(' ')
    event.channel.send_embed do |e|
      e.title = "**#{event.user.name} set the topic**"

      e.description = topic.join(' ')
      e.color = '00FF00'
    end
  end

  command(:fixtopic) do |event|
    break unless event.user.id == CONFIG['owner_id']
    if event.channel.topic == ''
      event.channel.topic = '[+nt]'
      break
    else
      event.channel.topic = '[+nt] ' + event.channel.topic
    end
    event.respond 'Fixed!'
  end
end
