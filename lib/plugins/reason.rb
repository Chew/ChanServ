module Reason
  extend Discordrb::Commands::CommandContainer

  command(:reason, min_args: 2) do |event, caseid, *reason|
    unless %w[Oper Owner Admin Op Half-Op].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +h (half-op) or higher.'
        e.color = 'FF0000'
      end
      next
    end

    event.message.delete

    if caseid.to_i < 95
      event.respond 'This log happened prior to audit-log 2.0. The reason will not be able to change! Please try again.'
      break
    end

    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = Bot.channel(210_174_983_278_690_304).message(cases[caseid.to_i].to_i)

    embed = message.embeds[0]

    fields = embed.fields

    fields.each do |find|
      next unless find.name == 'Reason'
      fields.delete(find)
      fields[fields.length] = Discordrb::Webhooks::EmbedField.new(
        name: find.name,
        value: reason.join(' '),
        inline: true
      )
    end

    defe = []

    fields.each do |meme|
      defe[defe.length] = Discordrb::Webhooks::EmbedField.new(
        name: meme.name,
        value: meme.value,
        inline: true
      )
    end

    case embed.title.split(' | ')[0]
    when 'Ban'
      color = 0xFF0000
    when 'Kick'
      color = 0xFAD765
    when 'User Mode Updated'
      color = 0x2FFA76
    end

    message.edit(
      '', Discordrb::Webhooks::Embed.new(
            title: embed.title,

            fields: defe,

            color: color
          )
    )
    event.send_temporary_message("Reason for case #{caseid} set to: #{reason.join(' ')}", 10)
  end
end
