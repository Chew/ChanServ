module ListCommunities
  extend Discordrb::Commands::CommandContainer

  command(:listcommunities) do |event|
    event.message.delete
    event.channel.send_embed do |e|
      e.title = "Communities Available on #{event.server.name}"

      e.add_field(name: 'Gaming', value: "**Members**: #{bot.channel(424_379_734_483_533_845).permission_overwrites.count - 2}\n**Join**: `;j gaming`", inline: true)
      e.add_field(name: 'Crypto', value: "**Members**: #{bot.channel(424_379_772_219_424_779).permission_overwrites.count - 2}\n**Join**: `;j crypto`", inline: true)
      e.add_field(name: 'Programming', value: "**Members**: #{bot.channel(424_379_786_006_364_161).permission_overwrites.count - 2}\n**Join**: `;j programming`", inline: true)
      e.add_field(name: 'Tech', value: "**Members**: #{bot.channel(424_379_920_924_278_784).permission_overwrites.count - 2}\n**Join**: `;j tech`", inline: true)
      e.add_field(name: 'Music', value: "**Members**: #{bot.channel(424_379_929_539_641_345).permission_overwrites.count - 2}\n**Join**: `;j music`", inline: true)
      e.add_field(name: 'Pets', value: "**Members**: #{bot.channel(424_379_940_604_084_224).permission_overwrites.count - 2}\n**Join**: `;j pets`", inline: true)
      e.add_field(name: 'Memes', value: "**Members**: #{bot.channel(424_379_961_256_968_192).permission_overwrites.count - 2}\n**Join**: `;j memes`", inline: true)
      e.add_field(name: 'LGBT', value: "**Members**: #{bot.channel(424_663_031_482_679_316).permission_overwrites.count - 2}\n**Join**: `;j lgbt`", inline: true)
      e.add_field(name: 'Anime', value: "**Members**: #{bot.channel(425_422_889_374_842_891).permission_overwrites.count - 2}\n**Join**: `;j anime`", inline: true)

      e.color = '00FF00'
    end
  end
end
